graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 9
    prb 5
    x 112
    y 60
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 3
    prb 2
    x 109
    y 82
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 8
    prb 5
    x 39
    y 23
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 7
    prb 2
    x 59
    y 41
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 7
    prb 2
    x 85
    y 15
  ]
  edge [
    source 0
    target 5
    bandwith 965
    delay 125
  ]
  edge [
    source 1
    target 3
    bandwith 150
    delay 117
  ]
  edge [
    source 1
    target 4
    bandwith 649
    delay 453
  ]
  edge [
    source 1
    target 2
    bandwith 853
    delay 272
  ]
  edge [
    source 1
    target 6
    bandwith 388
    delay 488
  ]
]
