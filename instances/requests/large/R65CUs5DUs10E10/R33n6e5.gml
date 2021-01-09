graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 1
    prc 1
    ant 6
    prb 5
    x 42
    y 36
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 2
    prb 6
    x 75
    y 120
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 5
    prb 2
    x 71
    y 111
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 9
    prb 3
    x 113
    y 80
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 2
    prb 5
    x 115
    y 25
  ]
  edge [
    source 0
    target 5
    bandwith 580
    delay 135
  ]
  edge [
    source 0
    target 2
    bandwith 804
    delay 148
  ]
  edge [
    source 0
    target 4
    bandwith 130
    delay 408
  ]
  edge [
    source 0
    target 1
    bandwith 666
    delay 322
  ]
  edge [
    source 0
    target 3
    bandwith 871
    delay 150
  ]
]
