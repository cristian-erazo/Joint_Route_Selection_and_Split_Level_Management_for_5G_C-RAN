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
    prc 3
    ant 9
    prb 3
    x 73
    y 91
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 10
    prb 3
    x 84
    y 57
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 2
    prb 5
    x 89
    y 42
  ]
  edge [
    source 0
    target 4
    bandwith 279
    delay 481
  ]
  edge [
    source 1
    target 2
    bandwith 664
    delay 203
  ]
  edge [
    source 1
    target 3
    bandwith 895
    delay 127
  ]
]
