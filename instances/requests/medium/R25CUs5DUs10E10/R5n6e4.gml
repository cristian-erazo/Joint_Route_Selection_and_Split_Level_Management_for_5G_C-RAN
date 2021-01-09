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
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 2
    prb 6
    x 25
    y 12
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 2
    prb 5
    x 79
    y 74
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 7
    prb 6
    x 94
    y 99
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 5
    prb 4
    x 71
    y 46
  ]
  edge [
    source 0
    target 4
    bandwith 624
    delay 261
  ]
  edge [
    source 0
    target 5
    bandwith 944
    delay 414
  ]
  edge [
    source 1
    target 3
    bandwith 844
    delay 285
  ]
  edge [
    source 1
    target 2
    bandwith 648
    delay 124
  ]
]
