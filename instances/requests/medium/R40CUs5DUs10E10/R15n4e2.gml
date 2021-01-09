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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 5
    prb 2
    x 99
    y 18
  ]
  node [
    id 3
    label "3"
    type 1
    prc 5
    ant 9
    prb 4
    x 18
    y 35
  ]
  edge [
    source 0
    target 2
    bandwith 910
    delay 102
  ]
  edge [
    source 1
    target 3
    bandwith 910
    delay 203
  ]
]
